int main(){
    int arr[] = {5,5,5,5,5,5,5,5,5,5,5};
    int n = 11,i,j,hold,k;

    for(i=1;i<=n-1;++i)
        {
        for(j=0;j<i;++j)
           if(arr[j]<arr[i])
    /*To sort elements in ascending order change < to > in above line.*/
           {
               hold=arr[i];
               k=i;
               while(k!=j)
               {
                   arr[k]=arr[k-1];
                   --k;
               }
               arr[j]=hold;
           }
        }
        return arr[0]+arr[1]+arr[2]+arr[3];
}