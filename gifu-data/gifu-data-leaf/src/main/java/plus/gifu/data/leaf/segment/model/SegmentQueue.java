package plus.gifu.data.leaf.segment.model;

import java.util.concurrent.LinkedBlockingQueue;

public class SegmentQueue {

    private final LinkedBlockingQueue<Segment> value = new LinkedBlockingQueue<>();

    public LinkedBlockingQueue getValue() {
        return value;
    }

}
