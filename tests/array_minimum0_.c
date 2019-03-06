int main(){
     int arr[] = {1,5};
     int min = 50, i, n=2;
     for(i=0; i<n; i++)
         if(arr[i] < min){
           min = arr[i];
         }
     return min;
 }