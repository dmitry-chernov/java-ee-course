/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dchernov.orm;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author Dc
 */
@Singleton
@LocalBean
public class MergeObjectToExistingRecordByItsUniqueKey {

    private static final Logger LOG = Logger.getLogger(MergeObjectToExistingRecordByItsUniqueKey.class.getName());

    public <T> T setObjectId(T object, EntityManager em) {
        return setObjectId(object, em, new HashSet<>());
    }

    private <T> T setObjectId(T object, EntityManager em, Set<Object> visited) {
        if (object instanceof Object[] || object instanceof Iterable) {
            for (Object o : (object instanceof Object[] ? Arrays.asList(object) : (Iterable) object)) {
                setObjectId(o, em, visited);
            }
        } else {
            if (object != null && !visited.contains(object)) {
                visited.add(object);
                RetriveSql retriveSQL = classCache.get(object.getClass());
                if (retriveSQL == null) {
                    retriveSQL = new RetriveSql(object, em);
                    classCache.put(object.getClass(), retriveSQL);
                }
                for (AccessibleObject f : retriveSQL.recursiveFields) {
                    try {
                        Object recursive = retriveSQL.getMember(object, f);
                        if (recursive != null) {
//                    setOwner(recursive, object);
                            setObjectId(recursive, em, visited);
                        }
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException ex) {
                        Logger.getLogger(MergeObjectToExistingRecordByItsUniqueKey.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                List rs = retriveSQL.execQuery(em, object);
                retriveSQL.setId(object, rs.isEmpty() ? null : rs.get(0));
            }
        }
        return object;
    }
    private final Map<Class<?>, RetriveSql> classCache = Collections.synchronizedMap(new HashMap<>());

    class RetriveSql {

        AccessibleObject idMember;
        TreeMap<String, AccessibleObject> nameValueMap = new TreeMap<>();
        Set<AccessibleObject> recursiveFields = new HashSet<>();
        String query;

        public <T> RetriveSql(T object, EntityManager em) {
            String[] columnNames = getColumnNames(object);
            if (columnNames != null) {
                Set<String> columns = new HashSet<>(Arrays.asList(columnNames));
                Class c = object.getClass();
                addMembers(c.getMethods(), columns);
                for (Class cls = c; cls != null; cls = cls.getSuperclass()) {
                    addMembers(cls.getDeclaredFields(), columns);
                }
                createQuery(c, em);
            }

        }

        public <T> List execQuery(EntityManager em, T object) {
            Query q = em.createQuery(query);
            setParameters(q, object);
            LOG.log(Level.INFO, "{0} => {1}", new Object[]{query, q.toString()});
            return q.getResultList();
        }

        public <T> void setId(T object, Object id) {
            try {
                setMember(object, idMember, id);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException ex) {
                Logger.getLogger(MergeObjectToExistingRecordByItsUniqueKey.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        private void setMember(Object object, AccessibleObject member, Object value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
            boolean isAccessible = member.isAccessible();
            member.setAccessible(true);
            if (member instanceof Method) {
                String name = ((Method) member).getName();
                if (name.startsWith("get")) {
                    member = object.getClass().getMethod("set" + name.substring(3), ((Method) member).getReturnType());
                }
                ((Method) member).invoke(object, value);
            } else if (member instanceof Field) {
                ((Field) member).set(object, value);
            }
            member.setAccessible(isAccessible);
        }

        private Object getMember(Object object, AccessibleObject member) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
            Object r = null;
            boolean isAccessible = member.isAccessible();
            member.setAccessible(true);
            if (member instanceof Method) {
                String name = ((Method) member).getName();
                if (name.startsWith("set")) {
                    member = object.getClass().getMethod("get" + name.substring(3));
                }
                r = ((Method) member).invoke(object);
            } else if (member instanceof Field) {
                r = ((Field) member).get(object);
            }
            member.setAccessible(isAccessible);
            return r;
        }

        private <T> String[] getColumnNames(T object) {
            String[] r = null;
            Table t = object.getClass().getAnnotation(Table.class);
            if (t != null) {
                UniqueConstraint[] constr = t.uniqueConstraints();
                if (constr != null) {
                    for (UniqueConstraint uc : constr) {
                        if (uc.name() != null && uc.name().startsWith("IDENTITY_KEY_")) {
                            r = uc.columnNames();
                            break;
                        }
                    }
                }
            }
            return r;
        }

        public void setParameters(Query q, Object object) {
            int i = 1;
            for (Map.Entry<String, AccessibleObject> value : nameValueMap.entrySet()) {
                AccessibleObject ao = value.getValue();
                try {
                    Object v = getMember(object, ao);
                    q.setParameter(i++, v);
                } catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException | NoSuchMethodException ex) {
                    Logger.getLogger(MergeObjectToExistingRecordByItsUniqueKey.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        private void addMembers(AccessibleObject[] members, Set<String> columns) {
            for (AccessibleObject m : members) {
                if (m.isAnnotationPresent(Id.class)) {
                    idMember = m;
                }
                Column col = m.getAnnotation(Column.class);
                JoinColumn jc = m.getAnnotation(JoinColumn.class);
                if (col != null && columns.contains(col.name())
                        || jc != null && columns.contains(jc.name())) {
                    nameValueMap.put(getMemberName(m), m);
                }
                if (m.isAnnotationPresent(OneToMany.class)
                        || m.isAnnotationPresent(ManyToOne.class)
                        || m.isAnnotationPresent(ManyToMany.class)) {
                    recursiveFields.add(m);
                }
            }
        }

        private String getMemberName(AccessibleObject m) {
            String r = null;
            if (m instanceof Field) {
                r = ((Field) m).getName();
            } else if (m instanceof Method) {
                Matcher mt = xNameBehindGetter.matcher(((Method) m).getName());
                r = mt.find(0) ? mt.group(1).toLowerCase() + mt.group(2) : null;
            }
            return r;
        }

        private <T> void createQuery(Class<T> cls, EntityManager em) {
            Entity e = cls.getAnnotation(Entity.class);
            String entityName = e.name() == null ? cls.getSimpleName() : e.name();
            StringBuilder queryString = new StringBuilder("SELECT c.id FROM ")
                    .append(entityName)
                    .append(" c WHERE ");
            int i = 1;
            for (String f : nameValueMap.keySet()) {
                if (i > 1) {
                    queryString.append(" AND ");
                }
                queryString.append("c.").append(f).append(" = ?").append(i++);
            }
            query = queryString.toString();
        }
    }
    private final static Pattern xNameBehindGetter = Pattern.compile("^get(\\w)(\\w*)");
}
