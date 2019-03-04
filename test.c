int main(){
  int arr[] = {12, 11, 13, 5, 6, 17, 1};

  int c,d,n,t;
  n=7;
  for (c = 1 ; c <= n - 1; c++) {
    d = c;

    while ( d > 0 && arr[d-1] > arr[d]) {
      t          = arr[d];
      arr[d]   = arr[d-1];
      arr[d-1] = t;

      d--;
    }
  }

  return arr[0];
}