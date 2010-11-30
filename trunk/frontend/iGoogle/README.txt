Making gulliver gadget run in iGoogle

 1. Check out code using Steps mentioned at:
    https://code.google.com/p/gulliver/source/checkout

 2. Goto frontend/iGoogle directory and upload trippy_raw.xml on any web hosting platform     e.g Appengine or any other of your's choice.

 3. Obtain absolute url of this file. Let's say it is     http://twittertweetposting.appspot.com/static/trippy_raw.xml, now goto     www.google.com/ig and goto Add stuff section(Link on right hand side of your iGoogle     home page).
  
 4. Click on Add feed or gadget link left hand side of the page. Paste url obtained at step     3 and click add. Now go back to igoogle home page to see your gadget.

 5. You may also paste this url http://www.google.com/ig/directorytype=gadgets&url=www.gstatic.com/ig/modules/trippy/trippy.xml in your browser window to see live version.

6. Replace value of key=KEY in <script src="http://maps.google.com/maps?file=api&amp;v=2.155&amp;sensor=false&amp;key=KEY" type="text/javascript"></script> tag in trippy_raw.xml with your own generated key. You may generate your key via http://www.google.com/url?q=http://code.google.com/apis/maps/signup.html


In order to change code in css and js files. Please download code from respository, make appropriate changes and host them on any web server. Also please update new js or css path in xml file also to reflect new changes