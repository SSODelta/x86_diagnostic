int main(){
     int arr[] = {60,40,20,50,30,10,45,35,15,25,15};
     int min = 100, i, n=11;
     for(i=0; i<n; i++)
         if(arr[i] < min){
           min = arr[i];
         }
     return min;
 }