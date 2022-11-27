package geektime.spring.springbucks.mapper;

import geektime.spring.springbucks.model.Coffee;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

@Mapper
public interface CoffeeMapper {
    @Insert("insert into t_coffee (name, price, create_time, update_time)"
            + "values (#{name}, #{price}, now(), now())")
    @Options(useGeneratedKeys = true)
    int save(Coffee coffee);

    @Delete("delete from t_coffee where name = #{name}")
    public int deleteByName(String name);

    @Update("update t_coffee set price = #{price},update_time=now() where name = #{name}")
    public int update(Coffee coffee);


    @Select("select * from t_coffee where name = #{name}")
    @Results({
            @Result(id = true, column = "name", property = "name"),
            @Result(column = "create_time", property = "createTime"),
    })
    Coffee findByName(@Param("name") String name);

    @Select("select * from t_coffee order by id")
    List<Coffee> findAll();

    @Select("select * from t_coffee order by id")
    List<Coffee> findAllWithRowBounds(RowBounds rowBounds);

    @Select("select * from t_coffee order by id")
    List<Coffee> findAllWithParam(@Param("pageNum") int pageNum,
                                  @Param("pageSize") int pageSize);
}
