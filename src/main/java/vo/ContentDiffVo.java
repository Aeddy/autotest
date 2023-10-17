package vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 差异文件属性
 * @author: zhenqinl
 * @date: 2023/9/20 17:33
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentDiffVo {

    private String filename;
}
