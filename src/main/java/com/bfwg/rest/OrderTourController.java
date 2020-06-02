package com.bfwg.rest;


import com.bfwg.dto.OrderTourDto;
import com.bfwg.dto.TourDto;
import com.bfwg.model.GroupType;
import com.bfwg.model.OrderTour;
import com.bfwg.model.Tour;
import com.bfwg.model.User;
import com.bfwg.repository.GroupTypeRepository;
import com.bfwg.repository.OrderTourRepository;
import com.bfwg.repository.TourRepository;
import com.bfwg.service.UserService;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api")
public class OrderTourController {
    @Autowired
    private OrderTourRepository orderTourRepository;
    @Autowired
    private GroupTypeRepository groupTypeRepository;
    @Autowired
    private TourRepository tourRepository;
    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/orderTour/create", method = RequestMethod.POST)
    public ResponseEntity<Object> createTour(Principal user, @Valid @RequestBody OrderTourDto orderTourDto) {
        Optional<GroupType> groupType = groupTypeRepository.findById(orderTourDto.getGroupTypeId());
        if (!groupType.isPresent()) {
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("GROUP TYPE NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        Optional<Tour> tour = tourRepository.findById(orderTourDto.getTourId());
        if (!tour.isPresent()) {
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("TOUR NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }

        User userId = this.userService.findByUsername(user.getName());
        DateTime date = new DateTime();
        OrderTour orderTour = new OrderTour(orderTourDto);
        orderTour.setTourId(tour.get());
        orderTour.setUserId(userId);
        orderTour.setStatus(0);
        orderTour.setDate(date.getMillis());
        orderTour.setGroupTypeId(groupType.get());

        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("CREATE SUCCESS!")
                .setData(new OrderTourDto(orderTourRepository.save(orderTour)))
                .build(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/orderTour/getOne/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getOneTour(Principal user, @PathVariable Long id) {
        Optional<OrderTour> orderTour = orderTourRepository.findById(id);
        User userId = this.userService.findByUsername(user.getName());

        if (!orderTour.isPresent() || orderTour.get().getUserId().getId() != userId.getId()) {
            return new ResponseEntity<>(new RESTResponse.Success()
                    .setStatus(HttpStatus.NOT_FOUND.value())
                    .setMessage("NOT FOUND!")
                    .setData(null)
                    .build(), HttpStatus.NOT_FOUND);
        }
        OrderTourDto orderTourDto = new OrderTourDto(orderTour.get());
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(orderTourDto)
                .build(), HttpStatus.OK);
    }


    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/orderTour/getOrderByUser", method = RequestMethod.GET)
    public ResponseEntity<Object> getOrderByUser(Principal user,
                                                 @RequestParam(defaultValue = "0") String page,
                                                 @RequestParam(defaultValue = "5") String limit) {
        User userId = this.userService.findByUsername(user.getName());
        System.out.println(userId.getId());
        List<OrderTour> orderTour = orderTourRepository.findAllByUserIdId(userId.getId());
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(orderTour.stream().map(x -> new OrderTourDto(x)).collect(Collectors.toList()))
                .setPagination(new RESTPagination(Integer.parseInt(page),
                        Integer.parseInt(limit),
                        orderTour.size(),
                        orderTour.size()))
                .build(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/orderTour/getOneAdmin/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> getOneTourAdmin(Principal user, @PathVariable Long id) {
        Optional<OrderTour> orderTour = orderTourRepository.findById(id);
        OrderTourDto orderTourDto = new OrderTourDto(orderTour.get());
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(orderTourDto)
                .build(), HttpStatus.OK);
    }


    @RequestMapping(value = "/orderTour/getFavoriteOrder", method = RequestMethod.GET)
    public ResponseEntity<Object> getFavorite() {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        ValueComparator bvc = new ValueComparator(map);
        TreeMap<String, Integer> sorted_map = new TreeMap<>(bvc);
        ArrayList<Tour> tourArrayList = new ArrayList<>();
        List<Tour> tours = tourRepository.findAll();
        for (int i = 0; i < tours.size(); i++) {
            List<OrderTour> orderTours = orderTourRepository.findAllByTourIdId(tours.get(i).getId());
            map.put(String.valueOf(tours.get(i).getId()), orderTours.size());
        }
        sorted_map.putAll(map);
        List<String> arrayList = sorted_map.keySet().stream().map(String::toString).collect(Collectors.toList());
        if (arrayList.size() < 5) {
            for (int y = 0; y < arrayList.size(); y++) {
                Optional<Tour> tour = tourRepository.findById(Long.valueOf(arrayList.get(y)));
                tourArrayList.add(tour.get());
            }
        } else {
            for (int y = 0; y < 5; y++) {
                Optional<Tour> tour = tourRepository.findById(Long.valueOf(arrayList.get(y)));
                tourArrayList.add(tour.get());
            }
        }
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")
                .setData(tourArrayList.stream().map(x->new TourDto(x)).collect(Collectors.toList()))
                .build(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/orderTour/success-payment", method = RequestMethod.GET)
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


        ArrayList<OrderTour> orderTour = orderTourRepository.findByDateBetweenAndStatus(millisStart, millisEnd, 2);
        Collections.sort(orderTour, new DateSorter());

        for (int i = 0; i < orderTour.stream().map(x -> new OrderTourDto(x)).collect(Collectors.toList()).size(); i++) {
            OrderTourDto orderTourDto = new OrderTourDto(orderTour.get(i));
            String firstDateString = new SimpleDateFormat("MM").format(new Date(orderTourDto.getDate()));
            System.out.println(firstDateString);
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

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/orderTour/getAll", method = RequestMethod.GET)
    public ResponseEntity<Object> getAllTour(
            @RequestParam(defaultValue = "0") String page,
            @RequestParam(defaultValue = "5") String limit) {

        List<OrderTour> orderTours = orderTourRepository.findAll();
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("Success!")

                .setData(orderTourRepository.findAll(PageRequest.of(Integer.parseInt(page), Integer.parseInt(limit))).stream().map(x -> new OrderTourDto(x)).collect(Collectors.toList()))
                .setPagination(new RESTPagination(Integer.parseInt(page),
                        Integer.parseInt(limit),
                        orderTours.size(),
                        orderTours.size()))
                .build(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/orderTour/edit/{id}", method = RequestMethod.GET)
    public ResponseEntity<Object> editTour(@PathVariable Long id, @RequestParam String token, Principal user) {
        Optional<OrderTour> orderTour = orderTourRepository.findById(id);
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
                        new OrderTourDto(orderTourRepository.save(orderTour.get()))
                )
                .build(), HttpStatus.OK);
    }


    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/orderTour/delete/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Object> deleteOrderTour(@PathVariable Long id, Principal user) {
        Optional<OrderTour> orderTour = orderTourRepository.findById(id);
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
        orderTour.get().setGroupTypeId(null);
        orderTour.get().setTourId(null);
        orderTour.get().setUserId(null);
        orderTourRepository.save(orderTour.get());
        orderTourRepository.delete(orderTour.get());
        return new ResponseEntity<>(new RESTResponse.Success()
                .setStatus(HttpStatus.OK.value())
                .setMessage("DELETE SUCCESS!")
                .build(), HttpStatus.OK);
    }

    public class DateSorter implements Comparator {
        public int compare(Object firstObjToCompare, Object secondObjToCompare) {
            OrderTourDto fisrt = new OrderTourDto((OrderTour) firstObjToCompare);
            OrderTourDto second = new OrderTourDto((OrderTour) secondObjToCompare);

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

}
