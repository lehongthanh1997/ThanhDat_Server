package com.bfwg.rest;


import com.bfwg.dto.CarDto;
import com.bfwg.dto.HotelDto;
import com.bfwg.dto.OrderCarDto;
import com.bfwg.model.Car;
import com.bfwg.model.ModelCar;
import com.bfwg.model.OrderCar;
import com.bfwg.model.User;
import com.bfwg.repository.CarRepository;
import com.bfwg.repository.OrderCarRepository;
import com.bfwg.repository.UserRepository;
import com.bfwg.service.UserService;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.text.ParseException;


@RestController
@RequestMapping( value = "/api")
public class OrderCarController {
    @Autowired
    private OrderCarRepository orderCarRepository;

    @Autowired
    private CarRepository carRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/orderCar/getAll", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllOrderCar(
            @RequestParam(defaultValue = "0") String page,
            @RequestParam(defaultValue = "5") String limit) {

        List<OrderCar> orderCars = orderCarRepository.findAll();
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(orderCarRepository.findAll(PageRequest.of(Integer.parseInt(page), Integer.parseInt(limit))).stream().map(x -> new OrderCarDto(x)).collect(Collectors.toList()))
                .setPagination(new RESTPagination(Integer.parseInt(page),
                        Integer.parseInt(limit),
                        orderCars.size(),
                        orderCars.size()))
                .build(), HttpStatus.OK);
    }
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/orderCar/getOne/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getOneOrderCar(Principal user,@PathVariable Long id) {
        Optional<OrderCar> orderCar = orderCarRepository.findById(id);

        if (!orderCar.isPresent()){
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }

        OrderCarDto orderCarDto = new OrderCarDto(orderCar.get());
        if (userRepository.findByUsername(user.getName()).getId() != userRepository.findById(orderCarDto.getUserId()).get().getId()){
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.UNAUTHORIZED.value())
                    .setMessage("UNAUTHORIZED!")
                    .setData(null)
                    .build(), HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(orderCarDto)
                .build(), HttpStatus.OK);
    }


    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/orderCar/create", method = RequestMethod.POST)
    public ResponseEntity<Object> createCar(Principal user, @Valid @RequestBody OrderCarDto orderCarDto) {
        Optional<Car> car = carRepository.findById(orderCarDto.getCarId());
        if (!car.isPresent()){
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("MODEL CAR NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }


        DateTime date = new DateTime();

        OrderCar orderCar = new OrderCar(orderCarDto);
        orderCar.setCarId(car.get());
        orderCar.setDate(date.getMillis());
        orderCar.setUserId(userRepository.findByUsername(user.getName()));

        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("CREATE SUCCESS!")
                .setData(new OrderCarDto(orderCarRepository.save(orderCar)))
                .build(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/orderCar/getOrderByUser", method = RequestMethod.GET)
    public ResponseEntity<Object> getOrderCarByUser(Principal user,
                                                 @RequestParam(defaultValue = "0") String page,
                                                 @RequestParam(defaultValue = "5") String limit) {
        User userId = this.userService.findByUsername(user.getName());
        System.out.println(userId.getId());
        List<OrderCar> orderCarList = orderCarRepository.findAllByUserIdId(userId.getId());
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(orderCarList.stream().map(x -> new OrderCarDto(x)).collect(Collectors.toList()))
                .setPagination(new RESTPagination(Integer.parseInt(page),
                        Integer.parseInt(limit),
                        orderCarList.size(),
                        orderCarList.size()))
                .build(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/orderCar/edit/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Object> editOrderCar(Principal user,@PathVariable Long id,@Valid @RequestBody OrderCarDto orderCarDto){
        Optional<Car> car = carRepository.findById(orderCarDto.getCarId());
        if (!car.isPresent()){
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("CAR NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        Optional<User> user2 = userRepository.findById(orderCarDto.getUserId());
        if (!user2.isPresent()){
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("USER NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        Optional<OrderCar> orderCar = orderCarRepository.findById(id);
        if (!orderCar.isPresent()){
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("ORDER CAR NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }

        orderCar.get().setSeason(orderCarDto.getSeason());
        orderCar.get().setRental_day(orderCarDto.getRental_day());
        orderCar.get().setStart_day(orderCarDto.getStart_day());
        orderCar.get().setCarId(car.get());

        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("UPDATE SUCCESS!")
                .setData(new OrderCarDto(orderCarRepository.save(orderCar.get())))
                .build(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/orderCar/getOneAdmin/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getOneOrderCarAdmin(@PathVariable Long id) {
        Optional<OrderCar> orderCar = orderCarRepository.findById(id);
        if (!orderCar.isPresent()){
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        OrderCarDto orderCarDto = new OrderCarDto(orderCar.get());
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(orderCarDto)
                .build(), HttpStatus.OK);
    }
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/orderCar/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteOrderTour(@PathVariable Long id, Principal user) {
        Optional<OrderCar> orderCar = orderCarRepository.findById(id);
        if (!orderCar.get().getUserId().getUsername().equals(user.getName())) {
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.UNAUTHORIZED.value())
                    .setMessage("UNAUTHORIZED!")
                    .setData(null)
                    .build(), HttpStatus.UNAUTHORIZED);
        }
        if (!orderCar.isPresent()) {
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("ORDER CAR NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        orderCar.get().setCarId(null);
        orderCar.get().setUserId(null);
        orderCarRepository.save(orderCar.get());
        orderCarRepository.delete(orderCar.get());
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("DELETE SUCCESS!")
                .build(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/orderCar/success-payment", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllSuccessPaymentInMonth(Principal user,
                                                              @RequestParam("year") String year)
            throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.parseInt(year));
        cal.set(Calendar.DAY_OF_YEAR, 1);
        Date start = cal.getTime();

        cal.set(Calendar.YEAR, Integer.parseInt(year));
        cal.set(Calendar.MONTH, 11); // 11 = december
        cal.set(Calendar.DAY_OF_MONTH, 31);
        Date end = cal.getTime();

        long millisStart = start.getTime();
        long millisEnd = end.getTime();
        Double Jan = 0.0;
        Double Feb = 0.0;
        Double Mar = 0.0;
        Double Apr = 0.0;
        Double May = 0.0;
        Double June = 0.0;
        Double July = 0.0;
        Double Aug = 0.0;
        Double Sept = 0.0;
        Double Oct = 0.0;
        Double Nov = 0.0;
        Double Dec = 0.0;


        ArrayList<OrderCar> orderTour = orderCarRepository.findByDateBetweenAndStatus(millisStart, millisEnd, 2);
        Collections.sort(orderTour, new DateSorter());

        for (int i = 0; i < orderTour.stream().map(x -> new OrderCarDto(x)).collect(Collectors.toList()).size(); i++) {
            OrderCarDto orderTourDto = new OrderCarDto(orderTour.get(i));
            String firstDateString = new SimpleDateFormat("MM").format(new Date(orderTourDto.getDate()));
            if (Integer.parseInt(firstDateString) == 1) {
                Jan += orderTourDto.getPrice();
            }
            if (Integer.parseInt(firstDateString) == 2) {
                Feb += orderTourDto.getPrice();

            }
            if (Integer.parseInt(firstDateString) == 3) {
                Mar += orderTourDto.getPrice();
            }
            if (Integer.parseInt(firstDateString) == 4) {
                Apr += orderTourDto.getPrice();

            }
            if (Integer.parseInt(firstDateString) == 5) {
                May += orderTourDto.getPrice();

            }
            if (Integer.parseInt(firstDateString) == 6) {
                June += orderTourDto.getPrice();

            }
            if (Integer.parseInt(firstDateString) == 7) {
                July += orderTourDto.getPrice();

            }
            if (Integer.parseInt(firstDateString) == 8) {
                Aug += orderTourDto.getPrice();

            }
            if (Integer.parseInt(firstDateString) == 9) {
                Sept += orderTourDto.getPrice();

            }
            if (Integer.parseInt(firstDateString) == 10) {
                Oct += orderTourDto.getPrice();

            }
            if (Integer.parseInt(firstDateString) == 11) {
                Nov += orderTourDto.getPrice();
            }
            if (Integer.parseInt(firstDateString) == 12) {
                Dec += orderTourDto.getPrice();

            }
        }
        Map<Integer, Object> json = new HashMap();

        json.put(1, Jan);
        json.put(2, Feb);
        json.put(3, Mar);
        json.put(4, Apr);
        json.put(5, May);
        json.put(6, June);
        json.put(7, July);
        json.put(8, Aug);
        json.put(9, Sept);
        json.put(10, Oct);
        json.put(11, Nov);
        json.put(12, Dec);

        Map<Integer, Object> sortedMap = new TreeMap<Integer, Object>(json);


        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(sortedMap)
                .build(), HttpStatus.OK);
    }

    public class DateSorter implements Comparator {
        public int compare(Object firstObjToCompare, Object secondObjToCompare) {
            OrderCarDto fisrt = new OrderCarDto((OrderCar) firstObjToCompare);
            OrderCarDto second = new OrderCarDto((OrderCar) secondObjToCompare);

            String firstDateString = new SimpleDateFormat("dd/yyyy HH:mm:ss").format(new Date(fisrt.getDate()));
            String secondDateString = new SimpleDateFormat("dd/yyyy HH:mm:ss").format(new Date(second.getDate()));

            if (secondDateString == null || firstDateString == null) {
                return 0;
            }
            // Convert to Dates
            DateTimeFormatter dtf = DateTimeFormat.forPattern("dd/yyyy HH:mm:ss");
            DateTime firstDate = dtf.parseDateTime(firstDateString);
            DateTime secondDate = dtf.parseDateTime(secondDateString);

            if (firstDate.isAfter(secondDate)) return -1;
            else if (firstDate.isBefore(secondDate)) return 1;
            else return 0;
        }
    }

    class ValueComparator implements Comparator<String> {
        Map<String, Integer> base;

        public ValueComparator(Map<String, Integer> base) {
            this.base = base;
        }

        // Note: this comparator imposes orderings that are inconsistent with
        // equals.
        public int compare(String a, String b) {
            if (base.get(a) >= base.get(b)) {
                return -1;
            } else {
                return 1;
            } // returning 0 would merge keys
        }
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/orderCar/editToken/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> editTour(@PathVariable Long id, @RequestParam String token, Principal user) {
        Optional<OrderCar> orderTour = orderCarRepository.findById(id);
        if (!orderTour.get().getUserId().getUsername().equals(user.getName())) {
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.UNAUTHORIZED.value())
                    .setMessage("UNAUTHORIZED!")
                    .setData(null)
                    .build(), HttpStatus.UNAUTHORIZED);
        }
        if (!orderTour.isPresent()) {
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("ORDER TOUR NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        orderTour.get().setToken(token);
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("UPDATE SUCCESS!")
                .setData(
                        new OrderCarDto(orderCarRepository.save(orderTour.get()))
                )
                .build(), HttpStatus.OK);
    }

    @RequestMapping(value = "/orderCar/getFavoriteOrder", method = RequestMethod.GET)
    public ResponseEntity<Object> getFavorite() {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        ValueComparator bvc = new ValueComparator(map);
        TreeMap<String, Integer> sorted_map = new TreeMap<>(bvc);
        ArrayList<Car> carArrayList = new ArrayList<>();
        List<Car> cars = carRepository.findAll();
        for (int i = 0; i < cars.size(); i++) {
            List<OrderCar> orderTours = orderCarRepository.findAllByCarIdId(cars.get(i).getId());
            map.put(String.valueOf(cars.get(i).getId()), orderTours.size());
        }
        sorted_map.putAll(map);
        List<String> arrayList = sorted_map.keySet().stream().map(String::toString).collect(Collectors.toList());
        if (arrayList.size() < 5) {
            for (int y = 0; y < arrayList.size(); y++) {
                Optional<Car> car = carRepository.findById(Long.valueOf(arrayList.get(y)));
                carArrayList.add(car.get());
            }
        } else {
            for (int y = 0; y < 5; y++) {
                Optional<Car> car = carRepository.findById(Long.valueOf(arrayList.get(y)));
                carArrayList.add(car.get());
            }
        }
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(carArrayList.stream().map(x->new CarDto(x)).collect(Collectors.toList()))
                .build(), HttpStatus.OK);
    }



}
