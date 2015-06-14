# AndroidGCTest
An Android app to test GC behaviour on Android.  This app shows how the GC on Android runs especially when there are some weakly referenced objects or softly referenced objects.

## Usage
1. Launch the app
2. Click the **ADD** button under **Weak** to add a weakly referenced object. The generated object has the size as specified with the left input box. Note the objects have the strong reference as well. So even if you hit **RUN GC**, they are not garbage-collected.
3. Click the **CLEAR** button under **Weak** to remove any strong references from the weakly referenced objects.
4. Then, click **RUN GC** to see they are garbage-collected.
5. You can do the same operations for soft-references with the buttons under **Soft**. The rightmost text demonstrates which object survives. You can see a new number is appended to the list every time you hit **ADD** under **Soft**, which means an object named the number is generated. With this text, you can see how the soft references are garbage-collected when they have no strong reference.
6. Under **Strong**, you can add or clear strong-referenced objects. You can control the available heap size with those buttons.
7. **RUN GC** runs GC explicitly.
8. **REFRESH** refreshes the memory status.

## Screenshot
![](https://cloud.githubusercontent.com/assets/6446183/8150674/3889152e-132e-11e5-957c-3b98da6636ef.png)

## Demo
![](https://cloud.githubusercontent.com/assets/6446183/8150665/9463fd42-132d-11e5-8db0-0636cc0a6fc8.gif)
