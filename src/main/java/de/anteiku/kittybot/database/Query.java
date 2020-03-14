package de.anteiku.kittybot.database;

public class Query {

    private StringBuilder query;

    public Query() {
        query = new StringBuilder();
    }

    public Query add(String string) {
        this.query.append(string);
        return this;
    }

    public Query createTable(String name) {
        return add("CREATE TABLE `" + name + "` ");
    }

    public Query createTableIfNotExists(String name) {
        return add("CREATE TABLE IF NOT EXISTS`").add(name).add("` (\n");
    }

    public Query columns() {
        return
    }
}
