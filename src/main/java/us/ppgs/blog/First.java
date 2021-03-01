package us.ppgs.blog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class First {

	public static void main(String[] args) {
		
		List<String> l = new ArrayList<>();
		
		Stream.of("Tom", "disk", "harry", "me").forEach(n -> l.add(n));
		
		l.forEach((n) -> {System.out.println(n);System.out.println(n.toUpperCase());});
		
		Comparator<String> comp = (first, second) -> Integer.compare(first.length(), second.length());
		l.sort(comp);

		l.stream().filter(n -> !n.startsWith("h")).forEach(n -> System.out.println(n));
	}
}
