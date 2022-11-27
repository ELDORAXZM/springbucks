package geektime.spring.springbucks.mapper;

import geektime.spring.springbucks.model.CoffeeOrder;
import org.apache.ibatis.annotations.*;

@Mapper
public interface CoffeeOrderMapper {
    @Insert("insert into t_order (customer, state, create_time, update_time)"
            + "values (#{customer}, #{state}, now(), now())")
    @Options(useGeneratedKeys = true)
    int save(CoffeeOrder coffeeorder);

    @Select("select * from t_order where id = #{id}")
    @Results({
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "create_time", property = "createTime"),

    })
    CoffeeOrder findById(@Param("id") Long id);
}
