import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { IonicModule } from '@ionic/angular';

import { PostulateAsDelegateComponent } from './postulate-as-delegate.component';

describe('PostulateAsDelegateComponent', () => {
  let component: PostulateAsDelegateComponent;
  let fixture: ComponentFixture<PostulateAsDelegateComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ PostulateAsDelegateComponent ],
      imports: [IonicModule.forRoot()]
    }).compileComponents();

    fixture = TestBed.createComponent(PostulateAsDelegateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
