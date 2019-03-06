int main(){
     int arr[] = {31,32,33,34,35,2,36,37,38,39,40};
     int min = 50, i, n=11;
     for(i=0; i<n; i++)
         if(arr[i] < min){
           min = arr[i];
         }
     return min;
 }