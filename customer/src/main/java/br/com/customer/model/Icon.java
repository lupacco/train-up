package br.com.customer.model;

import br.com.customer.dto.response.IconGetResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "icon")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Icon {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String name;

    @Column
    private String url;

    public IconGetResponse toGetResponse(){
        return IconGetResponse.builder()
                .id(this.id)
                .name(this.name)
                .url(this.url)
                .build();
    }
}
