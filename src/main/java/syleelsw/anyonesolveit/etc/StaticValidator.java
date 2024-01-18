package syleelsw.anyonesolveit.etc;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StaticValidator {
    public static boolean isValidArea(String locations) {
        String[] split = locations.split(" ");
        String city;
        if(split.length >2 || (split.length == 1 && !split[0].equals("ALL") || split.length==0)){
            return false;
        }else if(split.length == 1){
            city =  "ALL";
        }else{
            city = split[1];
        }
        try{
            Locations area = Locations.valueOf(split[0]);
            validateLocations(area, city);
        }catch (IllegalArgumentException e){
            return false;
        }
        return true;
    }




    public static void validateLocations(Locations location, String city){
        List cities;
        switch (location){
            case ALL -> {
                return;
            }
            case 서울, 서울특별시 -> cities = List.of(
                    "강남구",
                    "강동구",
                    "강북구",
                    "강서구",
                    "관악구",
                    "광진구",
                    "구로구",
                    "금천구",
                    "노원구",
                    "도봉구",
                    "동대문구",
                    "동작구",
                    "마포구",
                    "서대문구",
                    "서초구",
                    "성동구",
                    "성북구",
                    "송파구",
                    "양천구",
                    "영등포구",
                    "용산구",
                    "은평구",
                    "종로구",
                    "중구",
                    "중랑구");
            case 경기도 -> cities = List.of(
                    "수원시",
                    "성남시",
                    "의정부시",
                    "안양시",
                    "부천시",
                    "광명시",
                    "평택시",
                    "동두천시",
                    "안산시",
                    "고양시",
                    "과천시",
                    "구리시",
                    "남양주시",
                    "오산시",
                    "시흥시",
                    "군포시",
                    "의왕시",
                    "하남시",
                    "용인시",
                    "파주시",
                    "이천시",
                    "안성시",
                    "김포시",
                    "화성시",
                    "광주시",
                    "양주시",
                    "포천시",
                    "여주시",
                    "연천군",
                    "가평군",
                    "양평군");
            default -> cities = new ArrayList();
        }
        if(!cities.stream().anyMatch(t -> t.equals(city))){
            throw new IllegalArgumentException("잘못된 도시명 입니다");
        }
    }

}
