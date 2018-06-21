package com.wlw135.nice_photo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;

;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button showBtn;
    private Button refreshBtn;
    private ImageView showImg;
    private Button preBtn;
    private ArrayList<Sister> data;
    private int curPos = 0; //当前显示的是哪一张
    private int page = 1;   //当前页数
    private PictureLoader loader;
    private SisterApi sisterApi;
    private DBControl mdbHelper;
    private SisterTask sisterTask;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      //  mdbHelper=DBControl.getInstance();
        mdbHelper=new DBControl(this);
        sisterApi = new SisterApi();
        loader = new PictureLoader();
        initData();
        initUI();
    }

    private void initData() {
        data = new ArrayList<>();
        new SisterTask(page).execute();
    }

    private void initUI() {
        showBtn = (Button) findViewById(R.id.show_btn);
        preBtn = (Button) findViewById(R.id.pre_btn);
        refreshBtn = (Button) findViewById(R.id.upd_btn);
        showImg = (ImageView) findViewById(R.id.show_Img);
        preBtn.setOnClickListener(this);
        showBtn.setOnClickListener(this);
        refreshBtn.setOnClickListener(this);

    }

    @Override public void onClick(View v) {
        switch (v.getId()) {
            case R.id.show_btn:

                if(data != null && !data.isEmpty()) {
                    if (curPos > 8) {
                        curPos = 0;
                    }
                    curPos++;
                    loader.load(showImg, data.get(curPos).getUrl());
                    preBtn.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.upd_btn:
                page++;
                new SisterTask(page).execute();//启动任务
                curPos = 0;
                break;
            case R.id.pre_btn:
                --curPos;
                if(curPos==0){
                    preBtn.setVisibility(View.INVISIBLE);
                }

                if (curPos < 0) {
                    curPos=0;
                    return;
                }
                if (curPos == data.size() - 1) {
                    sisterTask = new SisterTask(page);
                    sisterTask.execute();
                }
                loader.load(showImg, data.get(curPos).getUrl());
                break;
        }
    }

    private class SisterTask extends AsyncTask<Void,Void,ArrayList<Sister>> {//第二个繁星参数是void，第三个是用Array来反馈信息

        private int page;

        public SisterTask(int page) {
            this.page = page;
        }

        @Override
        protected ArrayList<Sister> doInBackground(Void... params) {//子线程
           // return sisterApi.fetchSister(10,page);
            ArrayList<Sister> result = new ArrayList<>();
            if (page < (curPos + 1) / 10 + 1) {
                ++page;
            }
            //判断是否有网络
            if (NetworkUtils.isAvailable(getApplicationContext())) {
                result = sisterApi.fetchSister(10, page);
                //查询数据库里有多少个妹子，避免重复插入
                if(mdbHelper.getSistersCount() / 10 < page) {
                    mdbHelper.insertSisters(result);
                }
            } else {
                result.clear();
                result.addAll(mdbHelper.getSistersLimit(page - 1, 10));
            }
            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<Sister> sisters) {
            super.onPostExecute(sisters);
            data.clear();
            data.addAll(sisters);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(data != null && !data.isEmpty()) {
                        loader.load(showImg, data.get(curPos).getUrl());
                        preBtn.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }

    /**
     * Buffers input from an {@link InputStream} for reading lines.
     *
     * <p>This class is used for buffered reading of lines. For purposes of this class, a line ends
     * with "\n" or "\r\n". End of input is reported by throwing {@code EOFException}. Unterminated
     * line at end of input is invalid and will be ignored, the caller may use {@code
     * hasUnterminatedLine()} to detect it after catching the {@code EOFException}.
     *
     * <p>This class is intended for reading input that strictly consists of lines, such as line-based
     * cache entries or cache journal. Unlike the {@link java.io.BufferedReader} which in conjunction
     * with {@link java.io.InputStreamReader} provides similar functionality, this class uses different
     * end-of-input reporting and a more restrictive definition of a line.
     *
     * <p>This class supports only charsets that encode '\r' and '\n' as a single byte with value 13
     * and 10, respectively, and the representation of no other character contains these values.
     * We currently check in constructor that the charset is one of US-ASCII, UTF-8 and ISO-8859-1.
     * The default charset is US_ASCII.
     */
    static class StrictLineReader implements Closeable {
      private static final byte CR = (byte) '\r';
      private static final byte LF = (byte) '\n';

      private final InputStream in;
      private final Charset charset;

      /*
       * Buffered data is stored in {@code buf}. As long as no exception occurs, 0 <= pos <= end
       * and the data in the range [pos, end) is buffered for reading. At end of input, if there is
       * an unterminated line, we set end == -1, otherwise end == pos. If the underlying
       * {@code InputStream} throws an {@code IOException}, end may remain as either pos or -1.
       */
      private byte[] buf;
      private int pos;
      private int end;

      /**
       * Constructs a new {@code LineReader} with the specified charset and the default capacity.
       *
       * @param in the {@code InputStream} to read data from.
       * @param charset the charset used to decode data. Only US-ASCII, UTF-8 and ISO-8859-1 are
       * supported.
       * @throws NullPointerException if {@code in} or {@code charset} is null.
       * @throws IllegalArgumentException if the specified charset is not supported.
       */
      public StrictLineReader(InputStream in, Charset charset) {
        this(in, 8192, charset);
      }

      /**
       * Constructs a new {@code LineReader} with the specified capacity and charset.
       *
       * @param in the {@code InputStream} to read data from.
       * @param capacity the capacity of the buffer.
       * @param charset the charset used to decode data. Only US-ASCII, UTF-8 and ISO-8859-1 are
       * supported.
       * @throws NullPointerException if {@code in} or {@code charset} is null.
       * @throws IllegalArgumentException if {@code capacity} is negative or zero
       * or the specified charset is not supported.
       */
      public StrictLineReader(InputStream in, int capacity, Charset charset) {
        if (in == null || charset == null) {
          throw new NullPointerException();
        }
        if (capacity < 0) {
          throw new IllegalArgumentException("capacity <= 0");
        }
        /*if (!(charset.equals(Util.US_ASCII))) {
          throw new IllegalArgumentException("Unsupported encoding");
        }*/


        this.in = in;
        this.charset = charset;
        buf = new byte[capacity];
      }

      /**
       * Closes the reader by closing the underlying {@code InputStream} and
       * marking this reader as closed.
       *
       * @throws IOException for errors when closing the underlying {@code InputStream}.
       */
      public void close() throws IOException {
        synchronized (in) {
          if (buf != null) {
            buf = null;
            in.close();
          }
        }
      }

      /**
       * Reads the next line. A line ends with {@code "\n"} or {@code "\r\n"},
       * this end of line marker is not included in the result.
       *
       * @return the next line from the input.
       * @throws IOException for underlying {@code InputStream} errors.
       * @throws EOFException for the end of source stream.
       */
      public String readLine() throws IOException {
        synchronized (in) {
          if (buf == null) {
            throw new IOException("LineReader is closed");
          }

          // Read more data if we are at the end of the buffered data.
          // Though it's an error to read after an exception, we will let {@code fillBuf()}
          // throw again if that happens; thus we need to handle end == -1 as well as end == pos.
          if (pos >= end) {
            fillBuf();
          }
          // Try to find LF in the buffered data and return the line if successful.
          for (int i = pos; i != end; ++i) {
            if (buf[i] == LF) {
              int lineEnd = (i != pos && buf[i - 1] == CR) ? i - 1 : i;
              String res = new String(buf, pos, lineEnd - pos, charset.name());
              pos = i + 1;
              return res;
            }
          }

          // Let's anticipate up to 80 characters on top of those already read.
          ByteArrayOutputStream out = new ByteArrayOutputStream(end - pos + 80) {
            @Override
            public String toString() {
              int length = (count > 0 && buf[count - 1] == CR) ? count - 1 : count;
              try {
                return new String(buf, 0, length, charset.name());
              } catch (UnsupportedEncodingException e) {
                throw new AssertionError(e); // Since we control the charset this will never happen.
              }
            }
          };

          while (true) {
            out.write(buf, pos, end - pos);
            // Mark unterminated line in case fillBuf throws EOFException or IOException.
            end = -1;
            fillBuf();
            // Try to find LF in the buffered data and return the line if successful.
            for (int i = pos; i != end; ++i) {
              if (buf[i] == LF) {
                if (i != pos) {
                  out.write(buf, pos, i - pos);
                }
                pos = i + 1;
                return out.toString();
              }
            }
          }
        }
      }

      public boolean hasUnterminatedLine() {
        return end == -1;
      }

      /**
       * Reads new input data into the buffer. Call only with pos == end or end == -1,
       * depending on the desired outcome if the function throws.
       */
      private void fillBuf() throws IOException {
        int result = in.read(buf, 0, buf.length);
        if (result == -1) {
          throw new EOFException();
        }
        pos = 0;
        end = result;
      }
    }
}