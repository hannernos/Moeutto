package com.ssafy.moeutto.domain.clothes.dto.response;

import com.ssafy.moeutto.domain.clothes.entity.Clothes;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class ClothesRegistResponseDto {

    @NotNull
    private Clothes clothes; // 옷 정보

    @Builder(toBuilder = true)
    public ClothesRegistResponseDto(Clothes clothes) {
        this.clothes = clothes;
    }
}