package com.example.whereshouldwego.common.util;
import org.hashids.Hashids;

public class RoomCodeUtil {
    private static final Hashids hashids = new Hashids("gallaemallae_salt", 6);

    public static  String encode(Long roomId){
        return hashids.encode(roomId);
    }
    public static Long decode(String roomCode){
        long[] decoded = hashids.decode(roomCode);
        return decoded.length >0 ? decoded[0]: null;
    }
}
