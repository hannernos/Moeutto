package com.ssafy.moeutto.domain.aiCheckOutfit.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PythonRequestClothesListItems {

    String largeCategoryId;
    int clothesId;
    String season;
    String color;
    int thickness;
    String textile;
    int frequency;

    @Builder(toBuilder = true)
    public PythonRequestClothesListItems(String largeCategoryId, int clothesId, String season, String color, int thickness, String textile, int frequency){
        this.largeCategoryId = largeCategoryId;
        this.clothesId = clothesId;
        this.season = season;
        this.color = color;
        this.thickness = thickness;
        this.textile = textile;
        this.frequency = frequency;
    }

}