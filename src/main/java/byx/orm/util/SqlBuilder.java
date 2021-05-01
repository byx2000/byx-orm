package byx.orm.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SQL字符串生成器
 *
 * @author byx
 */
public class SqlBuilder {
    private static class Clause {
        final String prefix, suffix, delimiter;
        final List<String> items = new ArrayList<>();

        private Clause(String prefix, String suffix, String delimiter) {
            this.prefix = prefix;
            this.suffix = suffix;
            this.delimiter = delimiter;
        }

        Clause addItem(String item) {
            items.add(item);
            return this;
        }

        @Override
        public String toString() {
            return prefix + String.join(delimiter, items) + suffix;
        }
    }

    private final List<Clause> clauses = new ArrayList<>();
    private final Map<String, Integer> book = new HashMap<>();

    private SqlBuilder addItem(String key, String prefix, String suffix, String delimiter, String item) {
        if (book.containsKey(key)) {
            clauses.get(book.get(key)).addItem(item);
        } else {
            clauses.add(new Clause(prefix, suffix, delimiter).addItem(item));
            book.put(key, clauses.size() - 1);
        }
        return this;
    }

    public SqlBuilder select(String item) {
        return addItem("select", "SELECT ", "", ", ", item);
    }

    public SqlBuilder from(String item) {
        return addItem("from", "FROM ", "", ", ", item);
    }

    public SqlBuilder where(String item) {
        return addItem("where", "WHERE ", "", " AND ", item);
    }

    public SqlBuilder update(String item) {
        return addItem("update", "UPDATE ", "", ", ", item);
    }

    public SqlBuilder set(String item) {
        return addItem("set", "SET ", "", ", ", item);
    }

    public SqlBuilder append(String item) {
        return addItem("append", "", "", " ", item);
    }

    public String build() {
        return clauses.stream().map(Clause::toString).collect(Collectors.joining(" "));
    }
}
