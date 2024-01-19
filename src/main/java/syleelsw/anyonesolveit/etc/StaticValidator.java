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
            case 인천광역시 -> cities = List.of(
                    "계양구",
                    "미추홀구",
                    "남동구",
                    "동구",
                    "부평구",
                    "서구",
                    "연수구",
                    "중구",
                    "강화군",
                    "옹진군");
            case 강원도, 강원특별자치도 -> cities = List.of(
                    "춘천시",
                    "원주시",
                    "강릉시",
                    "동해시",
                    "태백시",
                    "속초시",
                    "삼척시",
                    "홍천군",
                    "횡성군",
                    "영월군",
                    "평창군",
                    "정선군",
                    "철원군",
                    "화천군",
                    "양구군",
                    "인제군",
                    "고성군",
                    "양양군");
            case 충청북도 -> cities = List.of(
                    "청주시",
                    "충주시",
                    "제천시",
                    "보은군",
                    "옥천군",
                    "영동군",
                    "증평군",
                    "진천군",
                    "괴산군",
                    "음성군",
                    "단양군"
            );
            case 충청남도 -> cities = List.of("천안시",
                    "공주시",
                    "보령시",
                    "아산시",
                    "서산시",
                    "논산시",
                    "계룡시",
                    "당진시",
                    "금산군",
                    "부여군",
                    "서천군",
                    "청양군",
                    "홍성군",
                    "예산군",
                    "태안군");
            case 대전광역시 -> cities = List.of("대덕구", "동구", "서구", "유성구", "중구");
            case 세종특별자치시 -> cities = List.of("세종특별자치시");
            case 전라북도, 전북특별자치도-> cities = List.of("전주시",
                    "군산시",
                    "익산시",
                    "정읍시",
                    "남원시",
                    "김제시",
                    "완주군",
                    "진안군",
                    "무주군",
                    "장수군",
                    "임실군",
                    "순창군",
                    "고창군",
                    "부안군");
            case 전라남도 -> cities = List.of(
                    "목포시",
                    "여수시",
                    "순천시",
                    "나주시",
                    "광양시",
                    "담양군",
                    "곡성군",
                    "구례군",
                    "고흥군",
                    "보성군",
                    "화순군",
                    "장흥군",
                    "강진군",
                    "해남군",
                    "영암군",
                    "무안군",
                    "함평군",
                    "영광군",
                    "장성군",
                    "완도군",
                    "진도군",
                    "신안군");
            case 광주광역시 -> cities = List.of("광산구", "남구", "동구", "북구", "서구");
            case 경상북도 -> cities = List.of("포항시 북구",
                    "경주시",
                    "김천시",
                    "안동시",
                    "구미시",
                    "영주시",
                    "영천시",
                    "상주시",
                    "문경시",
                    "경산시",
                    "군위군",
                    "의성군",
                    "청송군",
                    "영양군",
                    "영덕군",
                    "청도군",
                    "고령군",
                    "성주군",
                    "칠곡군",
                    "예천군",
                    "봉화군",
                    "울진군",
                    "울릉군");
            case 경상남도 -> cities = List.of("창원시",
                    "진주시",
                    "통영시",
                    "사천시",
                    "김해시",
                    "밀양시",
                    "거제시",
                    "양산시",
                    "의령군",
                    "함안군",
                    "창녕군",
                    "고성군",
                    "남해군",
                    "하동군",
                    "산청군",
                    "함양군",
                    "거창군",
                    "합천군");
            case 부산광역시 -> cities = List.of("강서구",
                    "금정구",
                    "남구",
                    "동구",
                    "동래구",
                    "부산진구",
                    "북구",
                    "사상구",
                    "사하구",
                    "서구",
                    "수영구",
                    "연제구",
                    "영도구",
                    "중구",
                    "해운대구",
                    "기장군");
            case 대구광역시 -> cities = List.of(
                    "남구",
                    "달서구",
                    "동구",
                    "북구",
                    "서구",
                    "수성구",
                    "중구",
                    "달성군");
            case 울산광역시 -> cities = List.of("남구", "동구", "북구", "중구", "울주군");
            case 제주특별자치도 -> cities = List.of("서귀포시", "제주시");
            default -> cities = new ArrayList();
        }
        if(!cities.stream().anyMatch(t -> t.equals(city))){
            throw new IllegalArgumentException("잘못된 도시명 입니다");
        }
    }

}
