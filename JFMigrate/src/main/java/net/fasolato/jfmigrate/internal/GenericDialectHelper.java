package net.fasolato.jfmigrate.internal;

import net.fasolato.jfmigrate.builders.Data;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class GenericDialectHelper implements IDialectHelper {
    protected String querySeparator = ";";

    protected String getQueryValueFromObject(Object o) {
        if(o == null) {
            return null;
        }

        if(o instanceof Integer || o instanceof Double || o instanceof Float) {
            return String.format("%s", o);
        } else if(o instanceof String) {
            return String.format("'%s'", o);
        }

        return String.format("'%s'", o);
    }

    @Override
    public List<Pair<String, Object[]>> getInsertCommand(Data d) {
        List<Pair<String, Object[]>> toReturn = new ArrayList<>();

        for (Map<String, Object> m : d.getData()) {
            String sql = "";
            List<Object> values = new ArrayList<>();
            for(String k : m.keySet()) {
                values.add(m.get(k));
            }

            sql += String.format(" INSERT INTO %s (%s) VALUES (%s)%s ", d.getTableName(), String.join(",", m.keySet()), String.join(",", Collections.nCopies(m.keySet().size(), "?")), querySeparator);

            toReturn.add(new ImmutablePair<>(sql, values.toArray()));
        }

        return toReturn;
    }

    @Override
    public List<Pair<String, Object[]>> getDeleteCommand(Data d) {
        List<Pair<String, Object[]>> toReturn = new ArrayList<>();

        if (!d.isAllRows()) {
            for (Map<String, Object> w : d.getWhere()) {
                List<Object> values = new ArrayList<>();
                String sql = String.format(" DELETE FROM %s WHERE 1 = 1 ", d.getTableName());
                for (String k : w.keySet()) {
                    sql += String.format(" AND %s = ?", k);
                    values.add(w.get(k));
                }
                sql += querySeparator;

                toReturn.add(new ImmutablePair<>(sql, values.toArray()));
            }
        } else {
            String sql = String.format(" DELETE FROM %s%s ", d.getTableName(), querySeparator);

            toReturn.add(new ImmutablePair<>(sql, null));
        }

        return toReturn;
    }

    @Override
    public List<Pair<String, Object[]>> getUpdateCommand(Data d) {
        List<Pair<String, Object[]>> toReturn = new ArrayList<>();

        for (int i = 0; i < d.getData().size(); i++) {
            Map<String, Object> m = d.getData().get(i);

            String sql = String.format(" UPDATE %s SET ", d.getTableName());
            List<Object> values = new ArrayList<>();

            int j = 0;
            for (String k : m.keySet()) {
                sql += k + " = ? ";
                values.add(m.get(k));
                if (j < m.keySet().size() - 1) {
                    sql += ", ";
                }
                j++;
            }
            if (!d.isAllRows()) {
                sql += " WHERE 1 = 1 ";
                for (Map<String, Object> w : d.getWhere()) {
                    for (String k : w.keySet()) {
                        sql += " AND " + k + " = ? ";
                        values.add(w.get(k));
                    }
                }
            }
            sql += querySeparator;

            toReturn.add(new ImmutablePair<>(sql, values.toArray()));
        }

        return toReturn;
    }
}
