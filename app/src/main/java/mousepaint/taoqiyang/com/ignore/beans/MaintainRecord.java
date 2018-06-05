package mousepaint.taoqiyang.com.ignore.beans;

import java.util.Date;

/**
 * 维护记录
 */

public class MaintainRecord {
    public final Long id;

    public final Long monitorID;

    public final String record;

    public final Date createTime;

    public MaintainRecord(Long id, Long monitorID, String record, Date createTime) {
        this.id = id;
        this.monitorID = monitorID;
        this.record = record;
        this.createTime = createTime;
    }
}
