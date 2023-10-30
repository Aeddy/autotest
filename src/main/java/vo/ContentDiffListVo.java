package vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description:
 * @author: zhenqinl
 * @date: 2023/10/30 12:08
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentDiffListVo {

    private int diffListId;

    private String sourceName;

    private String targeName;

    private int totalNum;

    private List<ContentDiffVo> contentDiffVos;
}
