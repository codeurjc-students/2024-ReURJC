import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { eventGuardGuard } from './event-guard.guard';

describe('eventGuardGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => eventGuardGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
