public class MergeSort {

    public static void sortByUrgency(ServiceRequest[] array) {
        if (array == null || array.length <= 1) return;
        ServiceRequest[] temp = new ServiceRequest[array.length];
        mergeSort(array, temp, 0, array.length - 1);
    }

    private static void mergeSort(ServiceRequest[] array, ServiceRequest[] temp, int left, int right) {
        if (left >= right) return;

        int mid = left + (right - left) / 2;
        mergeSort(array, temp, left, mid);
        mergeSort(array, temp, mid + 1, right);
        merge(array, temp, left, mid, right);
    }

    private static void merge(ServiceRequest[] array, ServiceRequest[] temp, int left, int mid, int right) {
        for (int i = left; i <= right; i++) {
            temp[i] = array[i];
        }

        int i = left;
        int j = mid + 1;
        int k = left;

        while (i <= mid && j <= right) {
            if (temp[i].getUrgency() >= temp[j].getUrgency()) {
                array[k] = temp[i];
                i++;
            } else {
                array[k] = temp[j];
                j++;
            }
            k++;
        }

        while (i <= mid) {
            array[k] = temp[i];
            i++;
            k++;
        }
    }
}