package KindaLocarusApp.Models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "Devices")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Device
{
    private String imei;            // Серийный номер машины
    private String type;            // Тип конфигурации прибора (должен выбираться из существующих)
    private Boolean status;         // Состояние работоспособности лицензии (0 - блок / 1 - атив) (по-умолчанию 1) определяется автоматически сторонним сервисом
    private Instant issueDate;
    private Instant expirationDate;
    private String deviceDescription;         // Комментарий (не обязателен)
    private List<String> log;       // (List<String>?) Авто-лог ключевых моментов, не доступен для модераций, кроме суперадмина.
}
