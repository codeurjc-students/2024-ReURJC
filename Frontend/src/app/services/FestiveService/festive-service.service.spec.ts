import { TestBed } from '@angular/core/testing';
import { FestiveServiceService } from './festive-service.service';

describe('FestiveServiceService', () => {
  let service: FestiveServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FestiveServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
