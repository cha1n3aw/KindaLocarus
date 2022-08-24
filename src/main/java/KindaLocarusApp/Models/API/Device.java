package KindaLocarusApp.Models.API;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/** TODO: add device parameters */
@Data
@NoArgsConstructor
@Document(collection = "Devices")
public class Device
{
    private Integer sn = 0;
    private String imei;        // Серийный номер машины
    private String type;    // Тип конфигурации прибора (должен выбираться из существующих)
    private boolean status;   // Состояние работоспособности лицензии (0 - блок / 1 - атив) (по-умолчанию 1) определяется автоматически сторонним сервисом
    private Date IssueDate;
    private Date ExpirationDate;
    private String comment;     // Комментарий (не обязателен)
    private List<String> log;         // (List<String>?) Авто-лог ключевых моментов, не доступен для модераций, кроме суперадмина.
}
