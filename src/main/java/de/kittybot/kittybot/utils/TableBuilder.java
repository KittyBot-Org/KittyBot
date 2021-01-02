package de.kittybot.kittybot.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TableBuilder<T>{

	private final List<String> columnNames;
	private final List<Function<? super T, String>> stringFunctions;

	public TableBuilder(){
		columnNames = new ArrayList<>();
		stringFunctions = new ArrayList<>();
	}

	public TableBuilder<T> addColumn(String columnName, Function<? super T, ?> fieldFunction){
		columnNames.add(columnName);
		stringFunctions.add((p) -> String.valueOf(fieldFunction.apply(p)));
		return this;
	}

	public String build(Iterable<? extends T> elements){
		var columnWidths = computeColumnWidths(elements);

		var sb = new StringBuilder("```\n");
		for(int c = 0; c < columnNames.size(); c++){
			sb.append("│");
			var format = "%" + columnWidths.get(c) + "s";
			sb.append(String.format(format, columnNames.get(c)));
		}
		sb.append("|\n");
		for(int c = 0; c < columnNames.size(); c++){
			if(c == 0){
				sb.append("├");
			}
			if(c > 0){
				sb.append("┼");
			}
			sb.append(padLeft("", '─', columnWidths.get(c)));
		}
		sb.append("┤\n");

		for(T element : elements){
			for(int c = 0; c < columnNames.size(); c++){
				sb.append("|");
				var format = "%" + columnWidths.get(c) + "s";
				var f = stringFunctions.get(c);
				sb.append(String.format(format, f.apply(element)));
			}
			sb.append("|\n");
		}
		sb.append("\n```");
		return sb.toString();
	}

	private List<Integer> computeColumnWidths(Iterable<? extends T> elements){
		var columnWidths = new ArrayList<Integer>();
		for(int c = 0; c < columnNames.size(); c++){
			var maxWidth = computeMaxWidth(c, elements);
			columnWidths.add(maxWidth);
		}
		return columnWidths;
	}

	private static String padLeft(String s, char c, int length){
		while(s.length() < length){
			s = c + s;
		}
		return s;
	}

	private int computeMaxWidth(int column, Iterable<? extends T> elements){
		var n = columnNames.get(column).length();
		var f = stringFunctions.get(column);
		for(T element : elements){
			n = Math.max(n, f.apply(element).length());
		}
		return n;
	}

}
