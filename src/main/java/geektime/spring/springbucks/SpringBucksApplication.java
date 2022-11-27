package geektime.spring.springbucks;

import geektime.spring.springbucks.model.Coffee;
import geektime.spring.springbucks.service.CoffeeOrderService;
import geektime.spring.springbucks.service.CoffeeService;
import io.lettuce.core.ReadFrom;
import lombok.extern.slf4j.Slf4j;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Slf4j
@EnableTransactionManagement
@SpringBootApplication
@MapperScan("geektime.spring.springbucks.mapper")
public class SpringBucksApplication implements ApplicationRunner {
	@Autowired
	private CoffeeService coffeeService;
	@Autowired
	private CoffeeOrderService orderService;


	@Bean
	public RedisTemplate<String, Coffee> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Coffee> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}
	@Bean
	public LettuceClientConfigurationBuilderCustomizer customizer() {
		return builder -> builder.readFrom(ReadFrom.MASTER_PREFERRED);
	}
	public static void main(String[] args) {
		SpringApplication.run(SpringBucksApplication.class, args);
	}


	@Transactional
	public void runWithTransactionMNG(){
		//Transaction Manager
		log.info("3.增删该需要加入事务管理，通过注解：");
		Coffee c1  = new Coffee();
		c1.setName("Cappuccino");
		c1.setPrice(Money.of(CurrencyUnit.of("CNY"), 2000));
		c1.setCreateTime(new Date());
		c1.setUpdateTime(new Date());
		coffeeService.insertCoffee(c1);
		c1 =coffeeService.findOneCoffee("Cappuccino");
		c1.setPrice(Money.of(CurrencyUnit.of("CNY"), 3000));
		coffeeService.updateCoffee(c1);
		coffeeService.deleteCoffee(c1.getName());
	}


	@Override
	public void run(ApplicationArguments args) throws Exception {
		//CURD
		log.info("1.实现CURD，查询时需要实现翻页及根据主键批量查询：");
		Coffee coffee  = new Coffee();
		coffee.setName("LatteCoffee");
		coffee.setPrice(Money.of(CurrencyUnit.of("CNY"), 2000));
		coffee.setCreateTime(new Date());
		coffee.setUpdateTime(new Date());
		coffeeService.insertCoffee(coffee);
		coffee =coffeeService.findOneCoffee("LatteCoffee");
		coffee.setPrice(Money.of(CurrencyUnit.of("CNY"), 3000));
		coffeeService.updateCoffee(coffee);
		coffeeService.deleteCoffee(coffee.getName());
		coffeeService.pageCoffeeList(1,2);

		//Redis
		log.info("2.查询时增加将结果缓存到redis：");
		Coffee c = coffeeService.findOneCoffeeByRedis("espresso");
		c = coffeeService.findOneCoffeeByRedis("espresso");

		//Transaction Manager
		runWithTransactionMNG();


	}



}

