package com.skellybuilds.SCMC;

public class StringAr {
    public static String[] removeElement(String[] array, String element) {
        // Find the index of the element to remove
        int index = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(element)) {
                index = i;
                break;
            }
        }

        // If the element is not found, return the original array
        if (index == -1) {
            return array;
        }

        // Create a new array that is one element smaller
        String[] newArray = new String[array.length - 1];

        // Copy elements from the original array to the new array, skipping the element to remove
        for (int i = 0, j = 0; i < array.length; i++) {
            if (i != index) {
                newArray[j++] = array[i];
            }
        }

        return newArray;
    }

}
