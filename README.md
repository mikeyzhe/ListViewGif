ListViewGif
===========

Show Gif animation in a List View

There are several issues for this implementation:

1. Currently the downloaded gif is cached only within the GifView. once the gifview is been reused, the cache lost
2. Default AsyncTask in Android is running in a 1-size THread Pool, which means we can not download gif files simutanenously.


