# 分布式ID生成策略（gifu-data-leaf）

## 项目介绍

本项目是针对美团Leaf项目的Segment模式进行的改进开发。由于某些场景需要，本人在实际项目开发中使用了Segment模式进行ID生成。阅读代码后收获颇多,其中的一些思想帮我解决了棘手的问题。

> 美团Leaf项目是美团订单ID生成的一个需求。具体设计文档见：[ leaf 美团分布式ID生成服务 ](https://tech.meituan.com/MT_Leaf.html )。目前Leaf覆盖了美团点评公司内部金融、餐饮、外卖、酒店旅游、猫眼电影等众多业务线。
> > There are no two identical leaves in the world.
> >
> > 世界上没有两片完全相同的树叶。
> >
> ​							— 莱布尼茨

Leaf项目的Segment模式核心逻辑思想是很优秀的，在保证基本有序性的情况下，还能具有高性能与高扩展性。不过代码逻辑有点啰嗦，代码逻辑不够清晰，阅读起来比较费力。自己阅读后自己感觉还有很多可以优化空间，因此抽出自己闲暇时间开发了此项目。

本项目在开发过程中主要将代码逻辑进行认真梳理，各个环节进行逻辑解耦，使用JDK中成熟的数据结构替换掉原代码中的数据结构，力求代码逻辑清晰易懂，降低隐藏BUG的可能性。

## 主要变化

1.从数据库获取号码段Segment使用乐观锁方式进行并发控制。

2.使用LinkedBlockingQueue队列进行Segment的预加载。

3.充分利用懒加载，减少定时器的使用。

4.自适应步长增加最大和最小范围限制。

5.不存在的key将会自动创建。

## 项目情况
### 源代码位置
`https://github.com/yanghyu/gifu/tree/master/gifu-data/gifu-data-leaf`

### 核心代码
详见`com.github.yanghyu.gifu.data.leaf.segment.generater.IdGeneratorImpl`

### 如何使用
代码示例:`https://github.com/yanghyu/gifu/tree/master/gifu-data/gifu-data-service/gifu-data-service-leaf`

依赖引用:
```

    <dependencyManagement>
        <dependencies>
            <dependency>
                <artifactId>gifu-data-starter</artifactId>
                <groupId>com.github.yanghyu</groupId>
                <version>1.1.0-RELEASE</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    
    <dependencies>
        <dependency>
            <groupId>com.github.yanghyu</groupId>
            <artifactId>gifu-data-starter-leaf</artifactId>
        </dependency>
    </dependencies>
    
```




 

