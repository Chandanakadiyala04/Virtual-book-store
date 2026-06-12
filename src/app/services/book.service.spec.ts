import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { BookService } from './book.service';
import { AuthService } from './auth.service';

describe('BookService', () => {
  let service: BookService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [BookService, AuthService]
    });
    service = TestBed.inject(BookService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => { httpMock.verify(); });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all books', () => {
    const mockBooks = [
      { id: '1', title: 'Clean Code', author: 'Robert Martin', price: 499.99 },
      { id: '2', title: 'The Great Gatsby', author: 'Fitzgerald', price: 299.99 }
    ];
    service.getBooks().subscribe(books => {
      expect(books.length).toBe(2);
      expect(books[0].title).toBe('Clean Code');
    });
    const req = httpMock.expectOne(r => r.url.includes('/books'));
    expect(req.request.method).toBe('GET');
    req.flush(mockBooks);
  });

  it('should get book by id', () => {
    const mockBook = { id: '1', title: 'Clean Code', author: 'Robert Martin' };
    service.getBook('1').subscribe(book => {
      expect(book.id).toBe('1');
      expect(book.title).toBe('Clean Code');
    });
    const req = httpMock.expectOne(r => r.url.includes('/books/1'));
    expect(req.request.method).toBe('GET');
    req.flush(mockBook);
  });

  it('should get books by category', () => {
    const mockBooks = [{ id: '1', title: 'Clean Code', category: 'Technology' }];
    service.getBooksByCategory('Technology').subscribe(books => {
      expect(books.length).toBe(1);
    });
    const req = httpMock.expectOne(r => r.url.includes('Technology'));
    expect(req.request.method).toBe('GET');
    req.flush(mockBooks);
  });

  it('should have bookRefresh$ observable', () => {
    expect(service.bookRefresh$).toBeDefined();
  });
});
