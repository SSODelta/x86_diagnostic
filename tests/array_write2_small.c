int main(){
  int arr[] = {5, 19};
  int i;
  for(i=0; i<1; i++){
    arr[i+1]=arr[i];
  }
  return arr[0]+arr[1];
}