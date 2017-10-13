/*
 * John St. Belfield
 * 3/6/2016
 * 
 * quicksort modified to work with an array of arrays
 * 
 * "Stephen Majercik
 * 2 November 2016
 * 
 * This program implements the QuickSort algorithm for arrays of arrays of integers,
 * where the first array holds the values to be sorted, and corresponding indices 
 * are kept together in their respective arrays.
 * 
 * Code for the partition method taken from:
 * Introduction to Algorithms, 3rd Edition (MIT Press)
 * Thomas H. Cormen, Charles E. Leiserson, Ronald L. Rivest, and Clifford Stein."
 * 
 */


public class QuickSort {
	
	
	public QuickSort() {
	}

	// Purpose: Sort an array of arrays of integers using QuickSort, 
	//		where first array are the values to be sorted
	// Parameters: The array to be sorted.
	// Return Value: None.
	//	
	public static void quickSort(double[][] arr) {
		// Call the auxiliary method that does the recursion
		quickSortAux(arr, 0, arr[0].length - 1);
	}


	// Purpose: Sort a range of an array of integers using QuickSort.
	// Parameters: The array and the left and right indices of the 
	//             range to be sorted.
	// Return Value: None.
	//	
	public static void quickSortAux(double arr[][], int left, int right) {

		// Lists of size 0 or 1 are already sorted.
		if (left >= right) {
			return;
		}

		// Partition the array and obtain the index of the pivot
		int pivotIndex = partition(arr, left, right);
		
		// The pivot number is where it should be in the final
		// sorted list, so sort each side recursively, not 
		// including the pivot
		quickSortAux(arr, left, pivotIndex - 1);
		quickSortAux(arr, pivotIndex + 1, right);
	}


	// Purpose: Partition a range of an array of integers using
	//          the last integer as the pivot.
	// Parameters: The array and the left and right indices of the 
	//             range to be partitioned.
	// Return Value: The index of the final position of the pivot.
	//
	public static int partition(double[][] arr, int left, int right) {

		// Take rightmost item as the pivot.
		double pivot = arr[0][right];

		// Index i is the index of the rightmost item in the 
		// section of the partition that contains the items
		// less than or equal to the pivot, i.e. the left side;
		// there is no such section yet so set it to 1 less
		// than the index of the leftmost element.
		int i = left - 1;
		
		// Go through each item in the array and place it on the
		// right side of the partition, if smaller than the pivot.
		for (int j = left; j < right; ++j) {
			
			// If the item at j is <= pivot, then the item at 
			// j is the new rightmost item in the left half 
			// of the partition, so swap it with whatever is 
			// to the immediate right of the current i
			if (arr[0][j] <= pivot) {
				
				// Index i + 1 is the new index of the rightmost  
				// item in left half of partition, so increment it.
				++i;
				
				// Swap the item at j into the position of  the
				// rightmost item in the left half of the partition.
				double tmp = arr[0][i];
				double tmpValue = arr[1][i];
				arr[0][i] = arr[0][j];
				arr[1][i] = arr[1][j];
				arr[0][j] = tmp;	
				arr[1][j] = tmpValue;
			}
		}
		
		// Index i+1 is where the pivot should go and, since 
		// i is the index of the rightmost item in the section 
		// of the partition that contains the items less than 
		// or equal to the pivot, the item at i+1 will be
		// greater than the pivot, so it is safe to swap the 
		// item at i+1 with the pivot item.
		double tmp = arr[0][i+1];
		double tmpValue = arr[1][i+1];
		arr[0][i+1] = arr[0][right];
		arr[1][i+1] = arr[1][right];
		arr[0][right] = tmp;
		arr[1][right] = tmpValue;

		// Index i+1 is where the pivot ends up, so return that,
		return i + 1;
	}


}
