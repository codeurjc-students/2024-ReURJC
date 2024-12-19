import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { voteDelegateGuardGuard } from './vote-delegate-guard.guard';

describe('voteDelegateGuardGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => voteDelegateGuardGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
