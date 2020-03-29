# petri网构建工具类

## 使用方法

首先利用`Util.setIidToLineMap`函数设置iidToLine文件的位置

```java
Util.setIidToLineMap("/home/wfb/毕设/calfuzzer/iidToLine.map");
```

先创建petri网类，然后首先创建root库所节点

```java
PetriNet net = new PetriNet();
net.createRootNode(0);
```