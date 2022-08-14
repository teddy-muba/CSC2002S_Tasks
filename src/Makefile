 JCC = javac

 JAVA = java

 RM = rm

 default:   MeanFilterSerial.class MeanFilterParallel.class MedianFilterSerial.class MedianFilterParallel.class


  MeanFilterSerial.class: MeanFilterSerial.java
  $(JCC) MeanFilterSerial.java

  MeanFilterParallel.class: MeanFilterParallel.java
  $(JCC) MeanFilterParallel.java

  MedianFilterSerial.class: MedianFilterSerial.java
  $(JCC) MedianFilterSerial.java

  MedianFilterParallel.class: MedianFilterParallel.java
  $(JCC) MedianFilterParallel.java

  run: $(MeanFilterSerial).class
  $(JVM)    $(MeanFilterSerial)
  run: $(MeanFilterParallel).class
  $(JVM)    $(MeanFilterParallel)

  run: $(MedianFilterSerial).class
  $(JVM)    $(MedianFilterSerial)

  run: $(MedianFilterParallel).class
  $(JVM)    $(MedianFilterParallel)

 clean:
 $(RM):= *.class
