package il.ac.tau.cs.software1.inventory;

/*
 * A simple array-based Inventory (Question 6).
 */
public class NaiveInventory implements Inventory {

	private static final int INITIAL_CAPACITY = 100;

	private Collectible[] items_array;
	private int items_count;

	public NaiveInventory() {
		this.items_array = new Collectible[INITIAL_CAPACITY];
		this.items_count = 0;
	}

	@Override
	public void collect(Collectible collectible) {
		if (this.items_array.length == this.items_count)
		{extend_array();}
		this.items_array[this.items_count] = collectible;
		this.items_count ++;
	}

	/*
	 * @pre item_array is full
	 * @post item_array length is doubled
	 */
	private void extend_array() {
		Collectible[] new_item_array = new Collectible[this.items_array.length * 2];
		for(int i = 0; i < this.items_count; i++)
		{
			new_item_array[i] = this.items_array[i];
		}
		this.items_array = new_item_array;
	}

	@Override
	public float getTotalWeight() {
		float weight = 0;
		for (int i = 0; i < this.items_count; i++)
		{
			weight += this.items_array[i].getWeight();
		}
		return weight;
	}

	@Override
	public int getCurrentCount() {
		return this.items_count;
	}

	// Returns the array of Collectibles that is stored in the field of this class
	public Collectible[] getCollArr() {
		return this.items_array;
	}
}
