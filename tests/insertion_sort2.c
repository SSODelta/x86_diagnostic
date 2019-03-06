int main(){
   int arr[] = {13,9,11,5,3,20,2,6,5};
   int i, key, j, n=9;
   for (i = 1; i < n; i++)
   {
       key = arr[i];
       j = i-1;

       /* Move elements of arr[0..i-1], that are
          greater than key, to one position ahead
          of their current position */
       while (j >= 0 && arr[j] > key)
       {
           arr[j+1] = arr[j];
           j = j-1;
       }
       arr[j+1] = key;
   }
   return arr[0] + arr[1] + arr[2] + arr[3];
}