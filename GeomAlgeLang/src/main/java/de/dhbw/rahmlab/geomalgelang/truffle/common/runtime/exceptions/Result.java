package de.dhbw.rahmlab.geomalgelang.truffle.common.runtime.exceptions;

import java.util.function.Function;

public sealed interface Result<OK, ERR> {

	ResultKind kind();

	default OK ok() {
		return okResult().get();
	}

	<NEW_ERR> Ok<OK, NEW_ERR> okResult();

	default ERR err() {
		return errResult().get();
	}

	<NEW_OK> Err<NEW_OK, ERR> errResult();

	default <NEW_OK> Result<NEW_OK, ERR> map(Function<OK, NEW_OK> func) {
		return switch (kind()) {
			case Ok ->
				new Ok<>(func.apply(ok()));
			case Err ->
				errResult();
		};
	}

	default OK orDefault(OK defaultValue) {
		return switch (kind()) {
			case Ok ->
				ok();
			case Err ->
				defaultValue;
		};
	}

	default OK orElseThrow(String message) {
		return switch (kind()) {
			case Ok ->
				ok();
			case Err ->
				throw new RuntimeException(message);
		};
	}

	default Result<ERR, OK> swap() {
		return switch (kind()) {
			case Ok ->
				new Err(ok());
			case Err ->
				new Ok(err());
		};
	}

	static sealed abstract class Wrapper<T> {

		private final T inner;

		public Wrapper(T inner) {
			this.inner = inner;
		}

		public T get() {
			return this.inner;
		}
	}

	static final class Ok<OkType, ErrType> extends Wrapper<OkType> implements Result<OkType, ErrType> {

		public Ok(OkType inner) {
			super(inner);
		}

		@Override
		public ResultKind kind() {
			return ResultKind.Ok;
		}

		@Override
		public Ok<OkType, ErrType> okResult() {
			return this;
		}

		@Override
		public Err<OkType, ErrType> errResult() {
			throw new UnsupportedOperationException();
		}
	}

	static final class Err<OkType, ErrType> extends Wrapper<ErrType> implements Result<OkType, ErrType> {

		public Err(ErrType inner) {
			super(inner);
		}

		@Override
		public ResultKind kind() {
			return ResultKind.Err;
		}

		@Override
		public Ok<OkType, ErrType> okResult() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Err<OkType, ErrType> errResult() {
			return this;
		}
	}

	static enum ResultKind {
		Ok,
		Err;
	}
}
