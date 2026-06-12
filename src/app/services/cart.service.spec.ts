import { TestBed } from '@angular/core/testing';
import { CartService } from './cart.service';

describe('CartService', () => {
  let service: CartService;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({ providers: [CartService] });
    service = TestBed.inject(CartService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should start with empty cart', () => {
    service.getCart().subscribe(items => {
      expect(items.length).toBe(0);
    });
  });

  it('should add item to cart', () => {
    const book: any = { id: '1', title: 'Clean Code', price: 499.99,
      author: 'Robert Martin', category: 'Technology',
      stock: 10, description: 'test', sellerId: 's1', imageUrl: '' };
    service.addToCart(book);
    service.getCart().subscribe(items => {
      expect(items.length).toBeGreaterThan(0);
    });
  });

  it('should remove item from cart', () => {
    const book: any = { id: '1', title: 'Clean Code', price: 499.99,
      author: 'Robert Martin', category: 'Technology',
      stock: 10, description: 'test', sellerId: 's1', imageUrl: '' };
    service.addToCart(book);
    service.removeFromCart('1');
    service.getCart().subscribe(items => {
      expect(items.find((i: any) => i.bookId === '1')).toBeUndefined();
    });
  });

  it('should clear cart', () => {
    const book: any = { id: '1', title: 'Clean Code', price: 499.99,
      author: 'Robert Martin', category: 'Technology',
      stock: 10, description: 'test', sellerId: 's1', imageUrl: '' };
    service.addToCart(book);
    service.clearCart();
    service.getCart().subscribe(items => {
      expect(items.length).toBe(0);
    });
  });

  it('should calculate total', () => {
    expect(service.getTotal()).toBeGreaterThanOrEqual(0);
  });
});
