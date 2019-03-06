int main(){
  int arr[] = {5, 19, 13,2};
  int i;
  for(i=0; i<3; i++){
    arr[i+1]=arr[i];
  }
  return arr[0]+arr[1]+arr[2]+arr[3];
}