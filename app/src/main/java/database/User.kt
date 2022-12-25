package database

import android.os.Parcel
import android.os.Parcelable

data class User(

    //var docRef: String?,
    var nama: String? = "",
    var time: String? = "",

    ) : Parcelable {
    constructor(parcel: Parcel) : this(

        parcel.readString(),
        parcel.readString(),
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

        parcel.writeString(nama)
        parcel.writeString(time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}