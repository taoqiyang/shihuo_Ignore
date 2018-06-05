package mousepaint.taoqiyang.com.ignore.beans;

import android.os.Parcel;
import android.os.Parcelable;

public class Monitor implements Parcelable{

    //basic info
    public final Long id;
    public final String name;
    /**0正常  -1异常*/
    public final Integer state;

    //address info
    public final String address;
    public final Double lat;
    public final Double lng;

    public Monitor(Long id, String name, Integer state, String address, Double lat, Double lng) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
    }

    public String getStateText() {
        switch(state){
            case -1:
                return "异常";
            default:
                return "正常";
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeInt(state);
    }

    public static final Parcelable.Creator<Monitor> CREATOR = new Creator<Monitor>()
    {
        @Override
        public Monitor[] newArray(int size)
        {
            return new Monitor[size];
        }

        @Override
        public Monitor createFromParcel(Parcel in)
        {
            return new Monitor(in.readLong(), in.readString(), in.readInt(), null, null, null);
        }
    };


}
