import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PagoBuscadorComponent } from './pago-buscador.component';

describe('PagoBuscadorComponent', () => {
  let component: PagoBuscadorComponent;
  let fixture: ComponentFixture<PagoBuscadorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PagoBuscadorComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PagoBuscadorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
