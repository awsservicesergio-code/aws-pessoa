package com.pessoa.aws.avro;

import com.pessoa.resources.avro.PessoaAvro;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayOutputStream;

public class SerializerAvro {

    public static byte[] serialize(PessoaAvro pessoaAvro) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DatumWriter<PessoaAvro> writer = new SpecificDatumWriter<>(PessoaAvro.class);
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out, null);
            writer.write(pessoaAvro, encoder);
            encoder.flush();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
