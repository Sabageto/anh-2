package com.thunga.web.service;

import com.thunga.web.entity.*;
import com.thunga.web.repository.BookRepository;
import com.thunga.web.repository.BookTranslatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private LanguageService languageService;

    @Autowired
    private PublisherService publisherService;

    @Autowired
    private SeriesService seriesService;

    @Autowired
    private TranslatorService translatorService;

    @Autowired
    private BookTranslatorRepository bookTranslatorRepository;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Book findById(Integer id) {
        return bookRepository.findById(id).orElse(null);
    }

    public List<Book> findByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    public void deleteById(Integer id) {
        bookRepository.deleteById(id);
    }

    public Page<Book> getBooks(int page, int limit, String sortBy,
                               Double priceMin, Double priceMax, Integer categoryId) {
        Sort sort;
        if ("title".equals(sortBy)) {
            sort = Sort.by("title").ascending();
        } else if ("title_desc".equals(sortBy)) {
            sort = Sort.by("title").descending();
        } else {
            sort = Sort.by("id").descending();
        }

        Pageable pageable = PageRequest.of(page - 1, limit, sort);

        // ← FIX: priceMin = 0 không tính là có filter min
        boolean hasMin = priceMin != null && priceMin > 0;
        boolean hasMax = priceMax != null;
        boolean hasCat = categoryId != null;

        if (hasCat) {
            if (hasMin && hasMax)
                return bookRepository.findByCategoryIdAndPriceBetween(categoryId, priceMin, priceMax, pageable);
            else if (hasMin)
                return bookRepository.findByCategoryIdAndPriceGreaterThanEqual(categoryId, priceMin, pageable);
            else if (hasMax)
                return bookRepository.findByCategoryIdAndPriceLessThanEqual(categoryId, priceMax, pageable);
            else return bookRepository.findByCategoryId(categoryId, pageable);
        } else {
            if (hasMin && hasMax) return bookRepository.findByPriceBetween(priceMin, priceMax, pageable);
            else if (hasMin) return bookRepository.findByPriceGreaterThanEqual(priceMin, pageable);
            else if (hasMax) return bookRepository.findByPriceLessThanEqual(priceMax, pageable);
            else return bookRepository.findAll(pageable);
        }
    }

    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public Book findBestSoldBook() {
        return bookRepository.findBestSoldBook();
    }

    public Book updateInfo(Book newBook, HttpServletRequest request, Translator translator) throws IOException {
        Book updateBook;
        Category category = categoryService.findByName(request.getParameter("categoryInfo"));
        Author author = authorService.findByName(request.getParameter("authorInfo"));

        // Get new entities
        String languageParam = request.getParameter("languageInfo");
        Language language = (languageParam != null && !languageParam.isEmpty())
                ? languageService.findByName(languageParam) : null;

        String publisherParam = request.getParameter("publisherInfo");
        Publisher publisher = (publisherParam != null && !publisherParam.isEmpty())
                ? publisherService.findByName(publisherParam) : null;

        String seriesParam = request.getParameter("seriesInfo");
        Series series = (seriesParam != null && !seriesParam.isEmpty())
                ? seriesService.findByName(seriesParam) : null;

        // Handle volumeNumber if series is selected
        String volumeNumberParam = request.getParameter("volumeNumber");
        Integer volumeNumber = null;
        if (volumeNumberParam != null && !volumeNumberParam.isEmpty()) {
            try {
                volumeNumber = Integer.valueOf(volumeNumberParam);
            } catch (NumberFormatException e) {
                // Ignore invalid volume number
            }
        }

        // Handle stock (number_in_stock)
        String stockParam = request.getParameter("stock");
        Integer stock = null;
        if (stockParam != null && !stockParam.isEmpty()) {
            try {
                stock = Integer.valueOf(stockParam);
            } catch (NumberFormatException e) {
                // Ignore invalid stock
            }
        }

        // Handle size
        String size = request.getParameter("size");

        if (newBook.getId() != null) {
            updateBook = bookRepository.findById(newBook.getId()).get();
            updateBook.setCategory(category);
            updateBook.setDescription(newBook.getDescription());
            updateBook.setDate_publication(newBook.getDate_publication());
            updateBook.setPrice(newBook.getPrice());
            updateBook.setLanguage(language);
            updateBook.setPublisher(publisher);
            updateBook.setSeries(series);
            updateBook.setVolumeNumber(volumeNumber);
            updateBook.setNumber_in_stock(stock);
            updateBook.setSize(size);
            updateBook.setUpdated_at(new Date());
        } else {
            updateBook = newBook;
            updateBook.setAuthor(author);
            updateBook.setCategory(category);
            updateBook.setLanguage(language);
            updateBook.setPublisher(publisher);
            updateBook.setSeries(series);
            updateBook.setVolumeNumber(volumeNumber);
            updateBook.setNumber_in_stock(stock != null ? stock : 0);
            updateBook.setNumber_sold(0);
            updateBook.setSize(size);
            updateBook.setCreated_at(new Date());
        }

        if (newBook.getFileData() != null && !newBook.getFileData().isEmpty()) {
            File uploadFolder = new File(System.getProperty("user.dir") + File.separator + uploadDir);
            if (!uploadFolder.exists()) {
                boolean created = uploadFolder.mkdirs();
                if (!created) {
                    throw new IOException("Could not create upload directory: " + uploadFolder.getAbsolutePath());
                }
            }

            String originalFilename = newBook.getFileData().getOriginalFilename();
            String sanitized = originalFilename == null ? "image" : originalFilename.replaceAll("[^a-zA-Z0-9.\\-_]", "_");
            String uniqueName = System.currentTimeMillis() + "_" + sanitized;
            File destFile = new File(uploadFolder, uniqueName);
            try (FileOutputStream fos = new FileOutputStream(destFile)) {
                fos.write(newBook.getFileData().getBytes());
            }
            updateBook.setImage("/uploads/" + uniqueName);
        }

        String imageUrl = newBook.getImage() != null ? newBook.getImage() : request.getParameter("imageUrl");
        if (imageUrl != null && !imageUrl.isEmpty()) {
            updateBook.setImage(imageUrl);
        }

        // Save the book first
        updateBook = bookRepository.save(updateBook);

        // Handle translator relationship
        if (translator != null && updateBook.getId() != null) {
            // Check if translator relationship already exists
            boolean translatorExists = false;
            if (updateBook.getBookTranslatorList() != null) {
                for (BookTranslator bt : updateBook.getBookTranslatorList()) {
                    if (bt.getTranslator().getId().equals(translator.getId())) {
                        translatorExists = true;
                        break;
                    }
                }
            }

            // Add translator if not exists
            if (!translatorExists) {
                BookTranslator bookTranslator = new BookTranslator();
                bookTranslator.setBook(updateBook);
                bookTranslator.setTranslator(translator);
                bookTranslator.setRole("Translator");
                bookTranslator.setCreated_at(new Date());
                bookTranslatorRepository.save(bookTranslator);
            }
        }

        return updateBook;
    }

    /**
     * Tìm kiếm TỰ ĐỘNG với phân trang
     */
    public Page<Book> searchAllWithPagination(String keyword, Integer page, Integer pageSize, String sortBy) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Page.empty();
        }

        keyword = keyword.trim();

        // Set để tránh trùng lặp
        Set<Book> resultSet = new LinkedHashSet<>();

        // 1. Tìm theo TITLE
        List<Book> booksByTitle = bookRepository.findByTitleContainingIgnoreCase(keyword);
        if (booksByTitle != null) {
            resultSet.addAll(booksByTitle);
        }

        // 2. Tìm theo AUTHOR
        List<Book> booksByAuthor = bookRepository.findByAuthor_NameContainingIgnoreCase(keyword);
        if (booksByAuthor != null) {
            resultSet.addAll(booksByAuthor);
        }

        // 3. Tìm theo CATEGORY
        Category category = categoryService.findByName(keyword);
        if (category != null) {
            List<Book> booksByCategory = bookRepository.findByCategory(category);
            if (booksByCategory != null) {
                resultSet.addAll(booksByCategory);
            }
        }

        // Chuyển Set về List
        List<Book> bookList = new ArrayList<>(resultSet);

        // Sắp xếp nếu có sortBy
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            switch (sortBy.toLowerCase()) {
                case "title":
                    bookList.sort((b1, b2) -> {
                        String t1 = b1.getTitle() != null ? b1.getTitle() : "";
                        String t2 = b2.getTitle() != null ? b2.getTitle() : "";
                        return t1.compareToIgnoreCase(t2);
                    });
                    break;
                case "price":
                    bookList.sort((b1, b2) -> {
                        Double p1 = b1.getPrice();
                        Double p2 = b2.getPrice();

                        if (p1 == null && p2 == null) return 0;
                        if (p1 == null) return 1;
                        if (p2 == null) return -1;

                        return p1.compareTo(p2);
                    });
                    break;
            }
        }

        // Tính toán phân trang
        int totalElements = bookList.size();
        int start = page * pageSize;
        int end = Math.min(start + pageSize, totalElements);

        // Lấy sublist cho trang hiện tại
        List<Book> pageContent = bookList.subList(start, end);

        // Tạo Page object
        Pageable pageable = PageRequest.of(page, pageSize);
        return new PageImpl<>(pageContent, pageable, totalElements);
    }

    /**
     * (Giữ lại method cũ cho backward compatibility)
     */
    public List<Book> search(String type, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }

        keyword = keyword.trim();
        List<Book> bookList = new ArrayList<>();

        switch (type.toLowerCase()) {
            case "title":
                bookList = bookRepository.findByTitleContainingIgnoreCase(keyword);
                break;

            case "author":
                bookList = bookRepository.findByAuthor_NameContainingIgnoreCase(keyword);
                break;

            case "category":
                Category category = categoryService.findByName(keyword);
                if (category != null) {
                    bookList = bookRepository.findByCategory(category);
                }
                break;

            default:
                bookList = bookRepository.findByTitleContainingIgnoreCase(keyword);
                break;
        }

        return bookList != null ? bookList : new ArrayList<>();
    }

    /**
     * Tìm sách theo category và tất cả children của nó với phân trang
     */
    public Page<Book> findByCategoryWithPagination(Integer categoryId, Integer page, Integer pageSize, String sortBy) {
        // Lấy tất cả category IDs (bao gồm chính nó và children)
        List<Integer> categoryIds = categoryService.getAllCategoryIdsInHierarchy(categoryId);

        // Tìm tất cả sách thuộc các categories này
        List<Book> allBooks = bookRepository.findByCategoryIdIn(categoryIds);

        // Sắp xếp nếu có sortBy
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            switch (sortBy.toLowerCase()) {
                case "title":
                    allBooks.sort(Comparator.comparing(
                            Book::getTitle,
                            Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)
                    ));
                    break;
                case "price":
                    allBooks.sort(Comparator.comparing(
                            Book::getPrice,
                            Comparator.nullsLast(Comparator.naturalOrder())
                    ));
                    break;
            }
        }

        // Tính toán phân trang
        int totalElements = allBooks.size();
        int start = page * pageSize;
        int end = Math.min(start + pageSize, totalElements);

        if (start >= totalElements) {
            return new PageImpl<>(new ArrayList<>(), PageRequest.of(page, pageSize), totalElements);
        }

        // Lấy sublist cho trang hiện tại
        List<Book> pageContent = allBooks.subList(start, end);

        // Tạo Page object
        Pageable pageable = PageRequest.of(page, pageSize);
        return new PageImpl<>(pageContent, pageable, totalElements);
    }

    public boolean hasCompletedOrderWithBook(User user, Book book) {
        if (user == null || user.getOrderList() == null) {
            return false;
        }

        for (Order order : user.getOrderList()) {
            if ("Completed".equals(order.getStatus())) {
                if (order.getOrderDetailList() != null) {
                    for (OrderDetail orderDetail : order.getOrderDetailList()) {
                        if (orderDetail.getBook().getId().equals(book.getId())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public List<Book> findByTitleIgnoreCase(String title) {
        return bookRepository.findByTitleIgnoreCase(title);
    }

    public boolean isDuplicateBook(String title, Integer authorId, Integer excludeBookId) {
        if (title == null || authorId == null) {
            return false;
        }

        List<Book> existingBooks = findByTitleIgnoreCase(title);

        if (existingBooks != null && !existingBooks.isEmpty()) {
            for (Book existingBook : existingBooks) {
                if (excludeBookId != null && existingBook.getId().equals(excludeBookId)) {
                    continue;
                }

                if (existingBook.getAuthor() != null &&
                        existingBook.getAuthor().getId().equals(authorId)) {
                    return true;
                }
            }
        }

        return false;
    }

    public List<Book> findBySeriesExcludingCurrent(Integer seriesId, Integer currentBookId) {
        return bookRepository.findBySeriesIdAndIdNot(seriesId, currentBookId);
    }

    public boolean validateBook(Book book) {
        if (book == null || book.getTitle() == null || book.getAuthor() == null) {
            return false;
        }

        return !isDuplicateBook(book.getTitle(), book.getAuthor().getId(), book.getId());
    }

    public String getBookValidationErrorMessage() {
        return "A book with the same title and author already exists";
    }
}