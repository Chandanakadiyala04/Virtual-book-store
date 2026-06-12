import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => { httpMock.verify(); localStorage.clear(); });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should have currentUserValue', () => {
    expect(service.currentUserValue).toBeDefined();
  });

  it('should return no token when not logged in', () => {
    localStorage.clear();
    expect(service.currentUserValue.token).toBeUndefined();
  });

  it('should call signin API', () => {
    const mockResponse = { token: 'test-token', username: 'john' };
    service.login('john', 'pass123').subscribe(res => {
      expect(res.token).toBe('test-token');
    });
    const req = httpMock.expectOne(r => r.url.includes('signin'));
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should call signup API', () => {
    const mockResponse = { message: 'User registered successfully!' };
    service.register({ username: 'john', email: 'john@gmail.com', password: 'pass' })
      .subscribe(res => {
        expect(res.message).toBe('User registered successfully!');
      });
    const req = httpMock.expectOne(r => r.url.includes('signup'));
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should logout and clear localStorage', () => {
    localStorage.setItem('currentUser', JSON.stringify({ token: 'test' }));
    service.logout();
    expect(localStorage.getItem('currentUser')).toBeNull();
  });
});
