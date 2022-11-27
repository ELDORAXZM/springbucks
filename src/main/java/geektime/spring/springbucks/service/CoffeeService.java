package geektime.spring.springbucks.service;

import com.github.pagehelper.PageInfo;
import geektime.spring.springbucks.mapper.CoffeeMapper;
import geektime.spring.springbucks.model.Coffee;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.exact;

@Slf4j
@Service
public class CoffeeService {
//    @Autowired
//    private CoffeeRepository coffeeRepository;
//
//    public List<Coffee> findAllCoffee() {
//        return coffeeRepository.findAll();
//    }
//
//    public Optional<Coffee> findOneCoffee(String name) {
//        ExampleMatcher matcher = ExampleMatcher.matching()
//                .withMatcher("name", exact().ignoreCase());
//        Optional<Coffee> coffee = coffeeRepository.findOne(
//                Example.of(Coffee.builder().name(name).build(), matcher));
//        log.info("Coffee Found: {}", coffee);
//        return coffee;
//    }

    private static final String CACHE = "springbucks-coffee";
    @Autowired
    private CoffeeMapper coffeeMapper;


    @Autowired
    private RedisTemplate<String, Coffee> redisTemplate;

    public Coffee findOneCoffee(String name) {
        Coffee coffee = coffeeMapper.findByName(name);
        log.info("select Coffee by name: {}", coffee);
        return coffee;
    }

    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void insertCoffee(Coffee coffee) {
        coffeeMapper.save(coffee);
        log.info("add Coffee: {}", coffee);
    }

    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void deleteCoffee(String name) {
        coffeeMapper.deleteByName(name);
        log.info("delete Coffee: {}", name);
    }

    @org.springframework.transaction.annotation.Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updateCoffee(Coffee coffee) {
        coffeeMapper.update(coffee);
        log.info("update Coffee price: {}", coffee);
    }


    //split page
    public PageInfo pageCoffeeList(int pageNum,int pageSize){
        List<Coffee> coffeeList = coffeeMapper.findAllWithRowBounds(new RowBounds(pageNum, pageSize));
        PageInfo page = new PageInfo(coffeeList);
        log.info("Coffee PageInfo: {}", page);
        return page;
    }

    public List<Coffee> findAllCoffee() {
        return coffeeMapper.findAll();
    }

    public Coffee findOneCoffeeByRedis(String name) {
        HashOperations<String, String, Coffee> hashOperations = redisTemplate.opsForHash();
        if (redisTemplate.hasKey(CACHE) && hashOperations.hasKey(CACHE, name)) {
            log.info("Get coffee by Redis {} ：", name);
            return hashOperations.get(CACHE, name);
        }
        Coffee coffee = coffeeMapper.findByName(name);
        if (null != coffee) {
            log.info("Get coffee and PUT into Redis {} ：", name);
            hashOperations.put(CACHE, name, coffee);
            redisTemplate.expire(CACHE, 1, TimeUnit.MINUTES);
        }
        return coffee;
    }
}
