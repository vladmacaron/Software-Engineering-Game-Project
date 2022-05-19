package client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Coordinates {
	private int x;
	private int y;
	
	public Coordinates(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public List<Coordinates> getNeighbours(int minX, int minY, int maxX, int maxY) {
		List<Coordinates> neighbors = new ArrayList<Coordinates>(8);
			for (int i = Math.max(x - 1, minX); i <= Math.min(x + 1, maxX); i++)
			   for (int j = Math.max(y - 1, minY); j <= Math.min(y + 1, maxY); j++) {
				   if (i == x && j == y)
					   continue;
				   neighbors.add(new Coordinates(i, j));
		}
		return neighbors;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coordinates other = (Coordinates) obj;
		return x == other.x && y == other.y;
	}
}
